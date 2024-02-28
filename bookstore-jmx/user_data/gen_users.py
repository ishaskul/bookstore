import csv
import random
import string

def dump_data(data, chunk_number):
    # Write data to a CSV file
    with open(f'user_chunk{chunk_number}.csv', mode='w', newline='') as file:
        writer = csv.writer(file)
        # writer.writerow(['username', 'password', 'firstname', 'email'])  # Header row
        writer.writerows(data)

def generate_password():
    password_length = random.randint(8, 12)
    password_characters = string.ascii_letters # + string.digits # + string.punctuation
    return ''.join(random.choice(password_characters) for i in range(password_length))

# Generate random data for the CSV file
data = []
chunk_number = 0 
for i in range(10000):
    username = ''.join(random.choices(string.ascii_lowercase, k=5))
    password = generate_password()
    first_name = ''.join(random.choices(string.ascii_uppercase, k=5))
    email = f"user{i}@vu.nl"

    data.append([username, password, first_name, email])

    if ((i % 100) == 0) and (i > 1):
        dump_data(data, chunk_number)
        data = []
        chunk_number += 1

print("CSV file created successfully with random user data.")
