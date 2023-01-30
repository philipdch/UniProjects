import numpy as np
import matplotlib.pyplot as plt

def f(x):
    return x**4 + 2*x**3 - 7*x**2 + 3

def mytan(x0, x1, tol, maxit):
    n = 0
    errors = []
    while( n < maxit):
        nom = f(x1)*(x1-x0)
        r = x1 - nom/(f(x1) - f(x0))
        errors.append(np.abs(r-x1))
        if(np.abs(r-x1) < tol):
            return r, errors
        n += 1
        x0 = x1
        x1 = r
    return r, errors

x = np.linspace(0, 2, 100)
plt.title('Graph of f(x) = x^4 +2x^3 - 7x^2 + 3')
plt.plot(x, f(x))
plt.grid(True)
plt.show()

tol = 10**-4
maxit = 15
solutions = set()

#As observed from the above graph, positive solutions are found in the interval [0,2]
#Therefore range of i should be between [0, 20]
for i in range(20):
    x0 = round(i*0.1, 1) #round to prevent floating-point precision problems
    x1 = round(x0 + 0.1, 1)
    ans, errors = mytan(x0, x1, tol, maxit)
    solutions.add(round(ans, 3))
    
    #Draw error graph for x0 = 1.4
    if(x0 == 1.4):
        print('Solution for x0 = 1.4: ' + str(ans))
        plt.plot(errors, label="x0 = 1.4")
        plt.xlabel('Number of iteratins')
        plt.ylabel('Error')
        plt.legend()
        plt.show()

print('Solutions:')
for sol in solutions:
    print(sol)

