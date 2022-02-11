import random #imports a random number generator
class Player:
        def __init__(self,name):
                self.name=name #player's name
                self.help_list=["skip this question","50-50"] #a list of helps available to each player
                self.points=0 #player's individual points
                self.i=-1 #player's own counter
        def removeHelp(self,helpName): #removes a help from player's help_list after it's been used
                self.help_list.remove(helpName)
        def setPoints(self): #increases player's points after he's answered a question correctly
                self.points+=10       
Questions=["Star Wars : Episode IV premeried on March 25,","The song 'Stairway to heaven' "+"is perfomed by :", "Which of the following paintings"+" was created by Vincent van Gogh?","The cause of Godel's (famous "+"mathematician) death in 1978 was :","Who discovered penicillin in 1928?","The 'Analytical Engine',one of the "+"first computers was created by :","Sodium Chloride is commonly referred to as :","In Marvel's 2008 movie 'Iron Man'"+"Tony Stark is portrayed by :","World War 2 officially ended on September 2, ","Greek philosopher ,famous for the 'Allegory of the cave'.","Larry Page is the co-founder of: ","The most valuable monument in Europe is the Eiffel Tower in France ,whose value has been calculated to be :"]
Answers=["A. 1983","B. 1975","C. 1989","D. 1977","A. The Rolling Stones","B. Led Zeppelin","C. Pink Floyd","D. Nirvana","A. The Starry Night","B. Mona Lisa","C. The Creation of Adam","D. Guernica","A. assassination","B. execution","C. starvation","D. drowning","A. Louis Pasteur","B. Alexander Fleming","C. Felix Hoffmann","D. Edward Jenner","A. Gottfried Leibniz","B. Charles Babbage","C. Alan Turing","D. Joseph Jackuard","A. Salt","B. Bleach","C. Water","D. Sugar","A. Tom Hanks","B. Chris Hemsworth","C. Chris Evans","D. Robert Downey Jr.","A. 1939","B. 1945","C. 1918","D. 1942","A. Socrates","B. Diogenes","C. Plato","D. Immanuel Kant","A. Microsoft","B. Apple","C. Google","D. Amazon","544 billion USD","200 billion USD","1 trillion USD","10 billion USD"]
CorrectAnswers=["D","B","A","C","B","B","A","D","B","C","C","A"] #the correct answer to each question
NumAnswers=[3,5,8,14,17,21,24,31,33,38,42,44]
print("In this quiz the players take turns to answer a total of 10 questions .") #game instructions
print("Each correct answer awards 10 points .Also each player can use up to 2 hints :")
print("50-50 ,which limits the number of possible answers to 2 and Skip the question ,")
print("which shows another question. Using only one hint awards +5 points at the end of")
print("the game. Using no hints gives the player 10 extra points.The winner at the end")
print("of the game is the player with the most points.")
print("Time to find out who really knows it all.")
print("")
def findCorrectAnswer(player,playerAnswer,correctAnswer,i): #finds the correct answer 
    if(playerAnswer==correctAnswer[i]):
        print("Correct! You gain 10 points.")
        print("")
        player.setPoints()
    else:
        print("Your answer was incorrect")
        print("")
def showQuestion(i,j,questionslist,answerlist,hintList): #prints the question ,available answer and gets the player's answer
        print(playerName+"'s turn. You have ",numberOfHints," available hint(s) : ")
        for w in range(len(hintList)):
            print(hintList[w],end=". ")
        print("")
        QuestionNo=i+1
        print(str(QuestionNo)+") ",Questions[i])
        print(" ")
        for j in range(4):
            print(Answers[(i*4)+j])
        answer=input("-Pick your answer : ")
        return answer
def show50_50(i,Answers,numAnswers,numberofhints,hintList):
    print("You have",numberofhints," help(s) left")   
    for w in range(len(hintList)):
        print(hintList[w],end=". ")
    print(" ")
    questionPosition=numAnswers[i]%4 
    while(True):
        randomNumber=random.randint(1,3) #generates a random number between 1-3
        if(randomNumber!=questionPosition): #prevents the second question shown to be the same as the correct one
                break
    if(questionPosition>((i*4)+randomNumber)%4):
        print(Answers[(i*4)+randomNumber])
        print(Answers[numAnswers[i]])
    else:
        print(Answers[numAnswers[i]])
        print(Answers[(i*4)+randomNumber])
    answer=input("-Pick your answer : ")
    return answer
def sortList(PointsList,playersList):
    for i in range(1,len(playersList)):
        for j in range(len(playersList),i,-1):
            if(PointsList[i-1]>PointsList[i]):
                temp=PointsList[i]
                PointsList[i]=PointsList[i-1]
                PointsList[i-1]=temp
                temp=playersList[i]
                playersList[i]=playersList[i-1]
                playersList[i-1]=temp
numOfPlayers=int(input("Enter number of players : "))
playersList=[]
PointsList=[]
for i in range(numOfPlayers): #gets the number of players 
    playersList.append(Player(input("Enter player's name : "))) #creates a list containing the players ,each item in the list corresponds to a different Player object)
count=len(Questions)-2
for i in range(count):
    for j in range(len(playersList)):
        currentPlayer=playersList[j]
        playerHints=playersList[j].help_list 
        numberOfHints = len(playerHints)
        playerName = currentPlayer.name
        currentPlayer.i+=1             #keeps track of each player's question 
        answer= showQuestion(currentPlayer.i,j,Questions[currentPlayer.i],Answers,playerHints)
        while(answer=="skip this question" or answer=="50-50"):
            if(answer=="skip this question"):  #if player chooses this help ,move to next question .Affects player's next questions
                currentPlayer.i+=1          #move player 1 question forward
                currentPlayer.removeHelp("skip this question") #remove help from player's list
                numberOfHints = len(playerHints)
                answer= showQuestion(currentPlayer.i,j,Questions,Answers,playerHints) #get new answer
            if(answer=="50-50"):
                currentPlayer.removeHelp("50-50") #remove this help from player's list
                numberOfHints = len(playerHints)
                answer=show50_50(currentPlayer.i,Answers,NumAnswers,numberOfHints,playerHints)
        findCorrectAnswer(currentPlayer,answer,CorrectAnswers,currentPlayer.i) #compare answer with item in CorrectAnswers list
        print(playerName+" has" ,currentPlayer.points,"points.")
        print("")
for i in range(len(playersList)): #create a List with the players' points
    if(len(playersList[i].help_list)==2):
        playersList[i].points+=10
    elif(len(playersList[i].help_list)==1):
        playersList[i].points+=5
    PointsList.append(playersList[i].points)
sortList(PointsList,playersList)
print("The winner of the game is ",playersList[len(playersList)-1].name," with",PointsList[len(playersList)-1]," points")
print("Ranking :")
for i in range(len(playersList)-1 ,-1 , -1):
    print(int(i+1),playersList[i].name,"",PointsList[i])

