import os
import unicodedata
import glob
import json
import re
import sys

def pre_process_dir_of_jsons(path, remove_prefix=True):
    files = glob.glob(os.path.join(path, '*.json'))
    for json_file in files:
        parsed_list = pre_process_jbs_json_file_for_spark(json_file, remove_prefix)
        parsed_list = expand_input(parsed_list)
        save_list_of_parsed_jsons_to_file(parsed_list, json_file)


def save_list_of_parsed_jsons_to_file(list_of_parsed_jsons, file_name):
    actual_file_name = file_name + ".spark"
    with open(actual_file_name, 'w',  encoding="utf-8") as f:
        for item in list_of_parsed_jsons:
            f.writelines(str(item)+'\n')


def pre_process_jbs_json_file_for_spark(json_file_name, remove_prefix):
    result = []
    with open(json_file_name, encoding="utf8") as json_file:
        json_data = json.load(json_file, encoding="utf-8")
        for entity in json_data["subjects"]:
            result.append((json.dumps(pre_process_jbs_obj_for_spark(entity, remove_prefix), ensure_ascii=False),
                           len(entity["jbo:name"])+1 if remove_prefix else 0))

    return result


def pre_process_jbs_obj_for_spark(json_object, remove_prefix):
    if remove_prefix:
        return {"uri": json_object["uri"],
                "text": remove_name_prefix(remove_vowels_hebrew(json_object["jbo:text"]), json_object["jbo:name"])
                }
    else:
        return {"uri": json_object["uri"],
                "text": remove_vowels_hebrew(json_object["jbo:text"])}


def remove_name_prefix(text, name_prefix):
    return text[len(name_prefix):]


def remove_vowels_hebrew(text):
    normalized = unicodedata.normalize('NFKD', text)
    flattened = "".join([c for c in normalized if not unicodedata.combining(c)])
    return flattened


def expand_input(list_of_jbs_dict):
    splitted_file = []
    for line in list_of_jbs_dict:
        dic = json.loads(line[0], encoding="utf-8")
        splitted_file.append({"uri": dic["uri"], "text": dic["text"]})

    return splitted_file

if __name__ == "__main__":
    directory_to_preprocess = sys.argv[1]
    pre_process_dir_of_jsons(directory_to_preprocess)
    # pre_process_dir_of_jsons("D:/Study/jbs-text/jbs-text/tanachMefarshim/")
