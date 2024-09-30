from flask import Flask, request, jsonify
import subprocess
import os

app = Flask(__name__)

def run_system_measurements(output_folder):
    cpu_utilization_command = f"~/bookstore/load_test_experiment/measure_system_cpu_uttilization.sh {output_folder}/system_cpu_data"
    power_consumption_command = f"~/bookstore/load_test_experiment/measure_system_power_consumption.sh {output_folder}/power_consumption_data"
    
    # Run the commands in the background
    subprocess.Popen(cpu_utilization_command, shell=True)
    subprocess.Popen(power_consumption_command, shell=True)

@app.route('/measure', methods=['POST'])
def measure():
    if not request.is_json:
        return jsonify({"error": "Request must be JSON"}), 400

    data = request.get_json()
    output_folder = data.get("output_folder")
    if not output_folder:
        return jsonify({"error": "'output_folder' is required"}), 400

    absolute_output_path = f"~/bookstore/load_test_experiment{output_folder.lstrip('.')}"

    run_system_measurements(absolute_output_path)
    
    return jsonify({"message": f"Measurement scripts triggered successfully! Generating data at path : {absolute_output_path}"}), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9119)