import csv
import os
import glob
import sys
import json
from datetime import datetime
import re
import csv

def transform_system_cpu_data_per_server_per_iteration(input_base_dir,output_base_dir, system_cpu_output_path ):
    if not os.path.exists(output_base_dir):
        os.makedirs(output_base_dir)

    # Loop through each folder (1 to 10)
    for i in range(1, 11):
        input_dir = os.path.join(input_base_dir, str(i), system_cpu_output_path)

        # Loop through each server machine
        for machine in green_lab_machines:
            # Get the file matching the 'cpu_usage_output_{machine}_*.txt' pattern
            file_pattern = os.path.join(input_dir, f'cpu_usage_output_{machine}_*.txt')
            files = glob.glob(file_pattern)
            
            # Ensure there's at least one file
            if not files:
                print(f"No files found for {machine} in {input_dir}")
                continue

            # Process each file found
            for input_file in files:
                output_file = os.path.join(output_base_dir, machine, system_cpu_output_path, f'cpu_usage_output_{machine}_iteration_{i}.csv')

                # Read from the input text file and write to the CSV file
                with open(input_file, 'r') as file:
                    lines = file.readlines()

                headers = ["Time", "CPU", "user", "nice", "system", "iowait", "steal", "idle"]

                output_dir = os.path.dirname(output_file)
                if not os.path.exists(output_dir):
                    os.makedirs(output_dir)

                with open(output_file, 'w', newline='') as csvfile:
                    csvwriter = csv.writer(csvfile)
                    csvwriter.writerow(headers)

                    for line in lines:
                        split_line = line.split()

                        if len(split_line) == 8 and not line.startswith("Average:") and "idle" not in line:
                            csvwriter.writerow(split_line)

def convert_timestamp(unix_timestamp):
    dt_object = datetime.utcfromtimestamp(unix_timestamp)
    return dt_object.strftime('%Y-%m-%d %H:%M:%S')

import csv
import os
import glob
import json
import re

def transform_per_container_cpu_util_per_server(input_base_dir, output_base_dir, system_cpu_output_path):
    for i in range(1, 11):
        input_dir = os.path.join(input_base_dir, str(i), system_cpu_output_path)

        # Loop through each server machine
        for machine in green_lab_machines:
            machine_name = 'gl2' if machine == 'GreenLab-STF' else machine
            file_pattern = os.path.join(input_dir, f'per_container_cpu_usage_{machine_name}_*.json')
            files = glob.glob(file_pattern)
            
            if not files:
                print(f"No files found for {machine_name} in {input_dir}")
                continue

            for input_file in files:
                with open(input_file, 'r') as json_file:
                    data = json.load(json_file)

                    for prometheus_result in data['data']['result']:
                        metric_info = prometheus_result['metric']
                        values = prometheus_result['values']
                        
                        # Determine container name based on cmdline
                        cmdline = metric_info.get("cmdline")
                        if "prometheus.yml" in cmdline:
                            container_name = "prometheus"
                        elif "scaphandreprometheus" in cmdline:
                            container_name = "scaphandre"
                        elif "consulagent" in cmdline:
                            container_name = "consul"
                        elif re.search(r'\/(.*?)-jar\/(.*?)\.jar$', cmdline):
                            match = re.search(r'\/(.*?)\.jar$', cmdline)
                            container_name = match.group(1)
                        elif "telegraf" in cmdline:
                            container_name = "telegraf"
                        elif "chronograf" in cmdline:
                            container_name = "chronograf"
                        elif "kapacitord" in cmdline:
                            container_name = "kapacitord"
                        elif "influxd" in cmdline:
                            container_name = "influxd"
                        else:
                            container_name = "unknown"

                        outputPathForPerContainerCSVs = f'{output_base_dir}/{machine}/{container_name}'
                        
                        # Create directory if it doesn't exist
                        if not os.path.exists(outputPathForPerContainerCSVs):
                            os.makedirs(outputPathForPerContainerCSVs)
                        
                        csv_file_path = os.path.join(outputPathForPerContainerCSVs, f'cpu_usage_output_{container_name}_iteration_{i}.csv')

                        csv_row = {
                            "timestamp": convert_timestamp(int(timestamp)),
                            "container": container_name,
                            "cpu_utilization": utilization,
                            "pid": metric_info.get("pid")
                        }

                        csv_headers = ["timestamp", "container", "cpu_utilization", "pid"]

                        # Check if file exists to append data without overwriting header
                        file_exists = os.path.isfile(csv_file_path)

                        # Open the CSV file in append mode
                        with open(csv_file_path, 'a', newline='') as csv_file:
                            writer = csv.DictWriter(csv_file, fieldnames=csv_headers)

                            # If file does not exist, write the headers
                            if not file_exists:
                                writer.writeheader()

                            # Write the CSV row
                            writer.writerow(csv_row)

    for i in range(1, 11):
        input_dir = os.path.join(input_base_dir, str(i), system_cpu_output_path)

        # Loop through each server machine
        for machine in green_lab_machines:
            if(machine == 'GreenLab-STF'):
                machine_name = 'gl2'
            else:
                machine_name = machine 
            file_pattern = os.path.join(input_dir, f'per_container_cpu_usage_{machine_name}_*.json')
            files = glob.glob(file_pattern)
            
            if not files:
                print(f"No files found for {machine_name} in {input_dir}")
                continue

            for input_file in files:
                with open(input_file, 'r') as json_file:
                    data = json.load(json_file)

                    for prometheus_result in data['data']['result']:
                        metric_info = prometheus_result['metric']
                        values = prometheus_result['values']
                        for timestamp, utilization in values:
                             formatted_timestamp = convert_timestamp(int(timestamp))
                             cmdline = metric_info.get("cmdline")
                             if ("prometheus.yml" in cmdline):
                                 container_name = "prometheus"
                             elif("scaphandreprometheus" in cmdline):
                                 container_name = "scaphandre"     
                             elif("consulagent" in cmdline):
                                 container_name = "consul"
                             elif re.search(r'\/(.*?)-jar\/(.*?)\.jar$', cmdline):  
                                 match = re.search(r'\/(.*?)\.jar$', cmdline)
                                 container_name = match.group(1)
                             elif("telegraf" in cmdline):
                                 container_name = "telegraf"
                             elif("chronograf" in cmdline):
                                 container_name = "chronograf"
                             elif("kapacitord" in cmdline):
                                 container_name = "kapacitord"
                             elif("influxd" in cmdline):
                                 container_name = "influxd"                                                                     
                             csv_row = {
                                    "timestamp": formatted_timestamp,
                                    "container": container_name,
                                    "cpu_utilization": utilization,
                                    "pid": metric_info.get("pid")
                                    }
                
                             csv_headers = ["timestamp", "container", "cpu_utilization", "pid"]
                             outputPathForPerContainerCSVs = f'{output_base_dir}/{machine}/{container_name}'
                             if not os.path.exists(outputPathForPerContainerCSVs):
                                os.makedirs(outputPathForPerContainerCSVs)
                             with open(outputPathForPerContainerCSVs, 'w', newline='') as csv_file:
                                csv_headers = ["timestamp", "container", "cpu_utilization", "pid"]
                                writer = csv.DictWriter(csv_file, fieldnames=csv_headers)
                                writer.writeheader()
                                writer.writerow(csv_row)



if len(sys.argv) < 2:
    print("Please specify the scenario name: python3 data_transformer.py <scenario-name> e.g. buy_books")
    sys.exit(1)
green_lab_machines = ['GreenLab-STF', 'gl5', 'gl6']
scenario = sys.argv[1]
input_base_dir = f'./{scenario}/'
output_base_dir = f'./{scenario}/transformed_data/'
system_cpu_output_path = 'system_cpu_data'

transform_system_cpu_data_per_server_per_iteration(input_base_dir,output_base_dir, system_cpu_output_path)
