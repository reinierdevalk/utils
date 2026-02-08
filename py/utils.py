import random
import string
import xml.etree.ElementTree as ET
from datetime import date
from io import StringIO
from lxml import etree
from py.constants import *
from xml.dom import minidom

LEN_ID = 8
XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace'


def get_namespaces(root: etree._Element): # -> dict[str, str]:
	"""
	Extract namespaces from XML content using lxml's nsmap.

	The default namespace (empty key) is renamed to 'mei'.
	The 'xml' namespace is added manually as it is implicit in XML.
	"""
	ns = {k or 'mei': v for k, v in root.nsmap.items()}
	ns['xml'] = XML_NAMESPACE

	return ns


def get_namespaces_ET(xml_contents: str): # -> dict:
	# There is only one namespace, whose key is an empty string -- replace the  
	# key with something meaningful ('mei'). See
	# https://stackoverflow.com/questions/42320779/get-the-namespaces-from-xml-with-python-elementtree/42372404#42372404
	# To avoid an 'ns0' prefix before each tag, register the namespace as an empty string. See
	# https://stackoverflow.com/questions/8983041/saving-xml-files-using-elementtree
	#
	# StringIO treats a string as a file-like object that can be iterated over
	ns = dict([node for _, node in ET.iterparse(StringIO(xml_contents), events=['start-ns'])])
#	ns = dict([node for _, node in ET.iterparse(path, events=['start-ns'])])
	ns['mei'] = ns.pop('')
	ET.register_namespace('', ns['mei'])
	ns['xml'] = 'http://www.w3.org/XML/1998/namespace'

	return ns


def get_main_MEI_elements(root: etree._Element, ns: dict): # -> tuple[etree._Element, etree._Element]:
	meiHead = root.find('mei:meiHead', ns)
	music = root.find('mei:music', ns)

	return (meiHead, music)


def get_main_MEI_elements_ET(root: ET.Element, ns: dict): # -> tuple:
	meiHead = root.find('mei:meiHead', ns)
	music = root.find('mei:music', ns)

	return (meiHead, music)


def collect_xml_ids(root: etree._Element, ns: dict) -> list[str]:
	prefix = 'xml'
	local_name = 'id'
	return root.xpath(f'//@{prefix}:{local_name}', namespaces=ns)


def collect_xml_ids_ET(root: ET.Element, key: str): # -> list:
	return [elem.attrib[key] for elem in root.iter() if key in elem.attrib]


def get_tuning(tuning: etree._Element, ns: dict): # -> str:
	tuning_p_o = [(c.get('pname'), int(c.get('oct'))) for c in tuning.findall('mei:course', ns)]
	return next((k for k, v in TUNINGS.items() if v == tuning_p_o), None)


def get_tuning_ET(tuning: ET.Element, ns: dict): # -> str:
	tuning_p_o = [(c.get('pname'), int(c.get('oct'))) for c in tuning.findall('mei:course', ns)]
	return next((k for k, v in TUNINGS.items() if v == tuning_p_o), None)


def add_unique_id(prefix: str, arg_xml_ids: list): # -> list:
	"""
	Generates a unique ID with the given prefix and adds it to given list.

	Args:
		prefix (str): The prefix for the ID.
		arg_xml_ids (list): The list of existing IDs.

	Returns:
		list: The updated list of IDs.
	"""
	while True:
		rand_id = ''.join(random.choices(string.ascii_letters + string.digits, k=(LEN_ID - len(prefix))))
		xml_id = prefix + rand_id
		if xml_id not in arg_xml_ids:
			arg_xml_ids.append(xml_id)
			break

	return arg_xml_ids


def pretty_print(elem: etree._Element): # -> None:
	etree.indent(elem)
	print(etree.tostring(elem, encoding='unicode'))


def pretty_print_ET(elem: ET.Element) -> str:
	rough_string = ET.tostring(elem, encoding='unicode')
	parsed = minidom.parseString(rough_string)

	# Remove whitespace-only text nodes
	def strip_whitespace_nodes(node):
		for child in list(node.childNodes):
			if child.nodeType == child.TEXT_NODE and not child.data.strip():
				node.removeChild(child)
			elif child.hasChildNodes():
				strip_whitespace_nodes(child)

	strip_whitespace_nodes(parsed)

	pretty = parsed.toprettyxml(indent="   ")
	# Remove XML declaration if not needed
	pretty = '\n'.join(line for line in pretty.split('\n') if line.strip() and not line.startswith('<?xml'))

	return pretty


def is_empty(elem: etree._Element): # -> bool:
	if elem.text == None:
		return len(elem) == 0
	else:
		return len(elem) == 0 and elem.text.strip() == ''


def remove_all_empty(elem: etree._Element, exceptions: list): # -> None
	"""
	Recursively removes all empty elements from the given element. 
	If the given element is in the given list of exceptions (which contains
	values of <element>.tag), it is not removed. 
	"""
	changed = True
	while changed:
		changed = False
		for e in reversed(list(elem.iter())):
			if is_empty(e) and e.tag not in exceptions and e.getparent() is not None:
				e.getparent().remove(e)
				changed = True


def parse_root(xml_contents: str): # -> etree._Element:
	return etree.fromstring(xml_contents.encode('utf-8'))


def remove_namespace_from_tag(tag: str): # -> str:
	if not '}' in tag:
		return tag
	else:
		return tag.split('}', 1)[1]


def parse_tree(xml_contents: str): # -> tuple[etree._ElementTree, etree._Element]:
	"""
	Basic structure of <mei>:

	<mei> 
	  <meiHead/>
	  <music>
	    ...
	    <score>
	      <scoreDef/>
	      <section/>
	    </score>
	  </music>
	</mei>   
	"""
	root = etree.fromstring(xml_contents.encode('utf-8'))
	tree = etree.ElementTree(root)

	return (tree, root)


def parse_tree_ET(xml_contents: str): # -> Tuple:
	"""
	Basic structure of <mei>:
	
	<mei> 
	  <meiHead/>
	  <music>
	    ...
	    <score>
	      <scoreDef/>
	      <section/>
	    </score>
	  </music>
	</mei>   
	"""
	tree = ET.ElementTree(ET.fromstring(xml_contents))
#	tree = ET.parse(path)
	root = tree.getroot()

	return (tree, root)


def unwrap_markup_elements(element: ET.Element, markup_elements: list): # -> None:
	"""
	Recursively unwraps markup elements inside of the given element.
	"""
	unwrapped = True
	while unwrapped:
		unwrapped = False

		# Create a parent map
		parents = {child: parent for parent in element.iter() for child in parent}
		# Find markup elements
		for elem in list(element.iter()):
			if elem.tag in markup_elements and elem in parents:
				parent = parents[elem]
				index = list(parent).index(elem)

				# Insert child at index in parent
				for child in list(elem):
					parent.insert(index, child)
					index += 1 # for correct order

				# Remove markup element itself
				parent.remove(elem)
				unwrapped = True


def find_first_elem_after(ind: int, elems_flat: list, tag: str):
	return next(
		(e for e in elems_flat[ind + 1:] if e.tag == tag), None
	)


def write_xml(root: ET.Element, filepath: str): # -> None:
	ET.ElementTree(root).write(
		filepath, encoding='unicode', xml_declaration=True
	)


def print_all_elements(root: ET.Element, xml_id_key: str): # -> None:
	count = 0
	for elem in root.iter():
		print(elem, elem.get(xml_id_key))
		count += 1
	print(f'{count} elements in total\n')


def print_all_labelled_elements(root: ET.Element, xml_id_key: str): # -> None:
	count = 0
	for elem in root.iter():
		if elem.get('label') is not None:
			print(elem.tag, elem.get(xml_id_key), elem.get('label'))
			count += 1
	print(f'{count} labelled elements in total\n')


def get_isodate():
	"""
	Returns the current date in ISO format (yyyy-mm-dd).
	""" 
	return date.today().isoformat()
