FROM telegraf

# Add in the configuration file from the local directory.
ADD telegraf.conf /etc/telegraf/telegraf.conf

# Optionally keep dockerize in case you need it later
COPY dockerize dockerize

# Command to run Telegraf directly without waiting for InfluxDB
CMD telegraf