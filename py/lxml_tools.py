from lxml import etree

XML_NAMESPACE = 'http://www.w3.org/XML/1998/namespace'


def get_namespaces(root: etree._Element, name: str) -> dict[str, str]:
	"""
	Extract namespaces from XML content using lxml's nsmap.
	The 'xml' namespace is added manually as it is implicit in XML.

	Args:
		root: The root element.
		name: The key to use for the default namespace (which has an empty key).

	Returns:
		A dictionary mapping namespace prefixes to URIs.
	"""
	ns = {k or name: v for k, v in root.nsmap.items()}
	ns['xml'] = XML_NAMESPACE

	return ns


def is_empty(elem: etree._Element) -> bool:
	if elem.text == None:
		return len(elem) == 0
	else:
		return len(elem) == 0 and elem.text.strip() == ''


def collect_xml_ids(root: etree._Element, ns: dict) -> list[str]:
	prefix = 'xml'
	local_name = 'id'
	return root.xpath(f'//@{prefix}:{local_name}', namespaces=ns)


def get_wrapper_elem(elem: etree._Element, parent: etree._Element) -> etree._Element:
	"""
	Gets the ancestor of the given elem that is a direct child of the 
	given parent. If elem itself is a direct child of parent, elem 
	itself is returned.

	Example:
	<measure>
		<staff ... />
		<add>
			<unclear>
				<dir ... />
			</unclear>
		</add>
		<fermata ... />
	</measure>

	get_wrapper_elem(dir, measure) returns <add> elem
	get_wrapper_elem(fermata, measure) returns <fermata> elem
	"""

	while elem.getparent() is not parent:
		elem = elem.getparent()

	return elem


def unwrap(elem: etree._Element) -> None:
	parent = elem.getparent()
	index = list(parent).index(elem)
	# Move children to parent at the same position
	for i, child in enumerate(list(elem)):
		parent.insert(index + i, child)
	parent.remove(elem)


def make_element(name: str, parent: etree._Element = None, atts: list[tuple[str, str]] = None) -> etree._Element:
	"""
	Convenience method for creating an lxml element or SubElement object with a one-liner. 
	Useful because, in the conventional way, any attributes that contain a dot in their 
	name must be set separately with set():

	e = etree.Element(name, att_1='<val_1>', att_2='<val_2>', ..., att_n='<val_n>')
	e.set('<att_with_dot>', '<val>')

	or 

	se = etree.SubElement(parent, name, att_1='<val_1>', att_2='<val_2>', ..., att_n='<val_n>')
	se.set('<att_with_dot>', '<val>')
	"""

	if atts is None:
		atts = []
	o = etree.Element(name) if parent is None else etree.SubElement(parent, name)
	for a in atts:
		o.set(a[0], a[1])

	return o


def find_first_elem_after(ind: int, elems_flat: list, tag: str) -> etree._Element | None:
	return next(
		(e for e in elems_flat[ind + 1:] if e.tag == tag), None
	)


def write_xml(root: etree._Element, filepath: str) -> None:
	etree.ElementTree(root).write(
		filepath, encoding='unicode', xml_declaration=True
	)


def pretty_print(elem: etree._Element) -> None:
	etree.indent(elem)
	print(etree.tostring(elem, encoding='unicode'))


def print_all_elements(root: etree._Element, xml_id_key: str) -> None:
	count = 0
	for elem in root.iter():
		print(elem, elem.get(xml_id_key))
		count += 1
	print(f'{count} elements in total\n')


def print_all_labelled_elements(root: etree._Element, xml_id_key: str) -> None:
	count = 0
	for elem in root.iter():
		if elem.get('label') is not None:
			print(elem.tag, elem.get(xml_id_key), elem.get('label'))
			count += 1
	print(f'{count} labelled elements in total\n')


def remove_all_empty(elem: etree._Element, exceptions: list) -> None:
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


def get_parent(elem: etree._Element, ns: str, tag: str) -> etree._Element:
	"""
	Finds the nearest ancestor of elem with the given tag in the given namespace.

	Args:
		elem: The element to start from.
		ns: The namespace in Clark notation, e.g., '{http://www.music-encoding.org/ns/mei}'.
		tag: The local name of the element, e.g., 'tabGrp'.

	Returns:
		The matching ancestor element, or None if not found.
	"""
	while elem is not None:
		if elem.tag == f'{ns}{tag}':
			return elem
		elem = elem.getparent()
	return None
