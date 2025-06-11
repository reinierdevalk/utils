import random
import string
import xml.etree.ElementTree as ET
from py.constants import *
from io import StringIO

LEN_ID = 8

def get_tuning(tuning: ET.Element, ns: dict): # -> str
	tuning_p_o = [(c.get('pname'), int(c.get('oct'))) for c in tuning.findall('mei:course', ns)]
	return next((k for k, v in TUNINGS.items() if v == tuning_p_o), None)


def add_unique_id(prefix: str, arg_xml_ids: list): # -> list
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


def handle_namespaces(xml_contents: str): # -> dict
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


def parse_tree(xml_contents: str): # -> Tuple
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


def get_main_MEI_elements(root: ET.Element, ns: dict): # -> tuple
	meiHead = root.find('mei:meiHead', ns)
	music = root.find('mei:music', ns)

	return (meiHead, music)


def collect_xml_ids(root: ET.Element, key: str): # -> list
	return [elem.attrib[key] for elem in root.iter() if key in elem.attrib]


def find_first_elem_after(ind: int, elems_flat: list, tag: str):
	return next(
		(e for e in elems_flat[ind + 1:] if e.tag == tag), None
	)


def write_xml(root: ET.Element, filepath: str): # -> None
	ET.ElementTree(root).write(
		filepath, encoding='unicode', xml_declaration=True
	)


def print_all_elements(root: ET.Element, xml_id_key: str): # -> None
	count = 0
	for elem in root.iter():
		print(elem, elem.get(xml_id_key))
		count += 1
	print(f'{count} elements in total\n')


def print_all_labelled_elements(root: ET.Element, xml_id_key: str): # -> None
	count = 0
	for elem in root.iter():
		if elem.get('label') is not None:
			print(elem.tag, elem.get(xml_id_key), elem.get('label'))
			count += 1
	print(f'{count} labelled elements in total\n')

