import csv
import os

# Given a csv file, splits it into N equal parts 
def split_file(input_file, num_parts):
    with open(input_file, newline='') as csv_file:
        reader = csv.reader(csv_file, delimiter=',', quotechar='"')
        rows = list(reader)
        num_rows = len(rows)
        header = rows[0]

        rows_per_file = num_rows +1 // num_parts  # calculate rows to be included in each file

        parts = []
        for i in range(num_parts):
            start = 1 + i*rows_per_file # calaculate start index
            end = start + rows_per_file # calculate end index
            parts.append([header] + rows[start:end]) # copy rows corresponding to part

        #create chunk filenames
        base_filename = os.path.splitext(os.path.basename(input_file))[0] # get base filename of input file from its path
        outdir = os.path.dirname(input_file)
        out_files = []
        for i in range(num_parts):
            fname = os.path.join(outdir, f"{base_filename}_part{i+1}.csv")
            out_files.append(fname)

        #write data to chunks
        for i in range(num_parts):
            with open(out_files[i], 'w', newline="") as file:
                writer = csv.writer(file)
                writer.writerows(parts[i])
            part_number = int(os.path.splitext(os.path.basename(out_files[i]))[0].split("_part")[1])
            print("Wrote part: ", part_number)
        


input_file = input("Give input CSV file: ")
num_parts = int(input("Specify number of chunks to be created from input: "))
split_file(input_file, num_parts)
