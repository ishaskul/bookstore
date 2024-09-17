import csv
import pandas as pd
import matplotlib.pyplot as plt
import os
import glob

green_lab_machines = ['GreenLab-STF', 'gl5', 'gl6']
input_dir = '../system_cpu_data/'
output_dir = '../transformed_data/'

# Prepare the plot
plt.figure(figsize=(12, 8))

# Dictionary to hold maximum time for scaling x-axis
max_times = []

# Loop through each machine
for machine in green_lab_machines:
    # Find all files matching the pattern
    file_pattern = os.path.join(input_dir, f'cpu_usage_output_{machine}_*.txt')
    files = glob.glob(file_pattern)
    
    # Ensure there are files found
    if not files:
        print(f"No files found for {machine} with pattern {file_pattern}")
        continue

    for input_file in files:
        # Generate output file path
        output_file = os.path.join(output_dir, f'cpu_usage_output_{machine}.csv')
        
        # Read and write data to CSV
        with open(input_file, 'r') as file:
            lines = file.readlines()

        headers = ["Time", "CPU", "user", "nice", "system", "iowait", "steal", "idle"]

        # Open the output CSV file
        with open(output_file, 'w', newline='') as csvfile:
            csvwriter = csv.writer(csvfile)
            csvwriter.writerow(headers)
            for line in lines:
                split_line = line.split()
                
                if len(split_line) == 8 and not line.startswith("Average:") and not "idle" in line:
                    csvwriter.writerow(split_line)

        print(f"Data has been written to '{output_file}'")

        # Load and process data
        df = pd.read_csv(output_file)

        required_columns = ['Time', 'idle']
        if not all(col in df.columns for col in required_columns):
            print(f"Error: The required columns are not present in the data for {input_file}.")
            continue

        df['CPU Utilization (%)'] = 100 - df['idle']
        df['Time'] = pd.to_datetime(df['Time'], format='%H:%M:%S')
        start_time = df['Time'].iloc[0]
        df['Time Relative'] = (df['Time'] - start_time).dt.total_seconds()

        # Append max time for this DataFrame to the list
        max_times.append(df['Time Relative'].max())

        # Plot CPU Utilization over Time
        plt.plot(df['Time Relative'].to_numpy(), df['CPU Utilization (%)'].to_numpy(), marker='o', linestyle='-', label=f'CPU Utilization {machine}')

# Enhance the plot with additional styling
plt.title('CPU Usage Comparison', fontsize=14, fontweight='bold')
plt.xlabel('Time (seconds from start)', fontsize=12)
plt.ylabel('CPU Utilization (%)', fontsize=14)
plt.grid(True, linestyle='--', alpha=0.7)

# Define x-ticks and labels
interval = 15
max_time = max(max_times)  # Use the maximum time collected from all files
xticks = range(0, int(max_time) + interval, interval)
plt.xticks(xticks, [f'{t}s' for t in xticks], fontsize=6)

plt.yticks(fontsize=12)
plt.tight_layout()

# Add a legend
plt.legend(fontsize=12)

# Save the plot to a file
plt.savefig('cpu_utilization_comparison_plot.png')

# Show the plot
plt.show()
