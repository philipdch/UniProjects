import numpy as np
from array import *

VECTORS_PATH = "."
x = 300

with open(VECTORS_PATH + "txd.text", 'r') as file:
    Str = file.read()

lines = Str.split('\n')

txd = []
print("loading...")
for i in range(len(lines)):
    txd.append(lines[i].split(','))

print("successful load.")

print("matrix creation...")
for i in range(len(txd)):
    for j in range(len(txd[i])):
        txd[i][j] = float(txd[i][j])

print("matrix was created successfully.")

print("SVD calculation...")

U, S, V = np.linalg.svd(txd, full_matrices=True, compute_uv=True, hermitian=False)
print(U.shape)
print(S.shape)
print(V.shape)
print("SVD calculated successfully.")

print("SVD calculation...")

Sk = [[0 for c in range(x)] for r in range(x)]

for i in range(x):
    Sk[i][i] = S[i]
print(len(Sk), len(Sk[0]))

Uk = U[:, :x]

Vk = V[:x, :]

print("SVD calculated...")

print("Saving...")

# function to write array to given file
def write(file, array):
    dest = VECTORS_PATH + file
    f = open(dest, "w")
    for i in range(len(array)):
        if i != 0:
            f.write('\n')
        for j in range(len(array[i])):
            if j != 0:
                f.write(',')
            f.write(str(array[i][j]))

# Write matrices
write("Uk.text", Uk)
write("Vk.text", Vk)
write("Sk.text", Sk)
print("Save completed...")
