import random
import string
from datetime import date


def add_unique_id(prefix: str, arg_xml_ids: list, len_id: int=8) -> list:
	"""
	Generates a unique ID with the given prefix and adds it to given list.

	Args:
		prefix (str): The prefix for the ID.
		arg_xml_ids (list): The list of existing IDs.

	Returns:
		list: The updated list of IDs.
	"""
	while True:
		rand_id = ''.join(random.choices(string.ascii_letters + string.digits, k=(len_id - len(prefix))))
		xml_id = prefix + rand_id
		if xml_id not in arg_xml_ids:
			arg_xml_ids.append(xml_id)
			break

	return arg_xml_ids


def get_isodate() -> str:
	"""
	Returns the current date in ISO format (yyyy-mm-dd).
	""" 
	return date.today().isoformat()
