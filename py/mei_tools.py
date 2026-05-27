from lxml import etree

from py.constants import *
from py.lxml_tools import is_empty

TUNINGS = {
		   D   : [('d', 4), ('a', 3), ('e', 3), ('c', 3), ('g', 2), ('d', 2)],
		   E   : [('e', 4), ('b', 3), ('f#', 3), ('d', 3), ('a', 2), ('e', 2)],
		   F   : [('f', 4), ('c', 4), ('g', 3), ('eb', 3), ('bb', 2), ('f', 2)],
		   F6Eb: [('f', 4), ('c', 4), ('g', 3), ('eb', 3), ('bb', 2), ('eb', 2)],
		   G5  : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3)],
		   G   : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3), ('g', 2)], 
		   G6F : [('g', 4), ('d', 4), ('a', 3), ('f', 3), ('c', 3), ('f', 2)], 
		   A   : [('a', 4), ('e', 4), ('b', 3), ('g', 3), ('d', 3), ('a', 2)], 
		   A6G : [('a', 4), ('e', 4), ('b', 3), ('g', 3), ('d', 3), ('g', 2)]
		  }
SHIFT_INTERVALS = {D: -5, E: -3, F: -2, F6Eb: -2, G: 0, G6F: 0, A: 2, A6G: 2}


def get_main_mei_elements(root: etree._Element, ns: dict) -> tuple[etree._Element, etree._Element]:
	meiHead = root.find('mei:meiHead', ns)
	music = root.find('mei:music', ns)

	return (meiHead, music)


def remove_empty_markup(editorial: list[str], markup_elements: list[str], id_index: dict) -> None:
	"""
	Recursively removes empty markup elements, starting each element with an ID 
	in 'editorial', going up until a non-markup or non-empty element is reached.
	"""
	for xml_id in editorial:
		elem = id_index.get(xml_id)
		if elem is None:
			continue
		_remove_empty_markup_ancestors(elem, markup_elements)


def _remove_empty_markup_ancestors(elem: etree._Element, markup_elements: list[str]) -> None:
	"""
	If elem is empty, remove it and check its parent.
	Continue up the tree while parents are empty markup elements.
	"""
	while elem is not None:
		parent = elem.getparent()
		# Stop if element is not empty
		if not is_empty(elem):
#		if len(elem) > 0 or elem.text:
			break
		# Stop if element is not a markup element
		if elem.tag not in markup_elements:
			break
		# Remove the empty markup element
		if parent is not None:
			parent.remove(elem)
		# Continue with parent
		elem = parent


def unwrap_markup_elements(element: etree._Element, markup_elements: list) -> None:
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
				break # restart with fresh parent map


def get_tuning(tuning: etree._Element, ns: dict) -> str | None:
	tuning_p_o = [(c.get('pname'), int(c.get('oct'))) for c in tuning.findall('mei:course', ns)]
	return next((k for k, v in TUNINGS.items() if v == tuning_p_o), None)


def get_mei_keysig(key: str) -> str:
	if int(key) == 0:
		return key
	else:
		return key + 's' if int(key) > 0 else str(abs(int(key))) + 'f'


def get_octave(midi_pitch: int) -> int:
	c = midi_pitch - (midi_pitch % 12)
	return int((c / 12) - 1)


def get_midi_pitch(course: int, fret: int, arg_tuning: str) -> int:
	# Determine the MIDI pitches for the open courses
	abzug = 0 if not '-' in arg_tuning else 2
	open_courses = [67, 62, 57, 53, 48, (43 - abzug)]
	if arg_tuning[0] != G:
		shift_interv = SHIFT_INTERVALS[arg_tuning[0]]
		open_courses = list(map(lambda x: x + shift_interv, open_courses))
	return open_courses[course - 1] + fret
