# importing the required module
import matplotlib.pyplot as plt
import csv
import os

x_threads = []
y_time = []

dir = "./outputs/"
file = "output_{}.csv"

for i in range(0, 4):

    csv_file = dir+file.format(i);
    with open(csv_file, encoding="utf8") as f:
        csv_reader = csv.reader(f)
        # skip the header
        next(csv_reader)
        # calculate total
        for line in csv_reader:
            x_threads.append(float(line[1]))
            y_time.append(float(line[3]))
    plt.scatter(x_threads, y_time, color= "green", marker= ".", s=30)


    # plt.scatter()
    plt.xlabel('x - Threads')
    plt.ylabel('y - Time')
    plt.title('Threaded speed test')
    # plt.legend()
    plt.savefig((csv_file + ".png"))