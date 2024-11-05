# TrackingApp
University Project

Frontend:Android fitness app that tracks your activity. It takes as an input a gpx file and calculates user specific/general statistics such as distance,time,eleveation,speed.

Backend:A distributed system  java program with a "master" and multiple "worker" nodes. It uses that deisgn to split,process and analyze date from a GPX(GPS file format) files in chunks.

GPX file format:

![image](https://github.com/user-attachments/assets/7faf5d7e-ea73-465e-9333-50c761ebddc9)


# How to Run This Code:
After compiling the code run the Master program:

java Master <port_listen> <worker_amount> <chunk_size> <worker_ip1> <worker_port1> <worker_ip2> <worker_port2> ...

- <port_listen>: Port the master server listens on.

- <worker_amount>: Number of worker nodes.

- <chunk_size>: Number of waypoints in each chunk sent to workers.

- <worker_ip> and <worker_port> pairs for each worker node.
