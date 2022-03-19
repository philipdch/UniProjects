# Author : FILIPPOS DOURACHALIS
# Date   : 6/11/2018
# Description : calculate Greatest Common Divisor (GCD) between two numbers and ask user for the correct answer

#t0 = a
#t1 = b
#t2 = y
#t3 = s
.text 
.globl main

main :
		la $a0 ,promptX
		li $v0 ,4      # print "Give A : "
		syscall
		
		li $v0 ,5      # read A
		syscall
		move $t0 ,$v0
		
		la $a0,promptY
		li $v0 ,4 
		syscall
		
		li $v0,5
		syscall
		move $t1 ,$v0
		
		la $a0, prompt
		li $v0 ,4
		syscall
		
		div $t0 ,$t1
		mfhi $t2
Loop1 : beq $t2 ,$zero ,CONTINUE
		move $t0 ,$t1
		move $t1 , $t2
		
		move $a0 , $t1
		li $v0 ,1
		syscall
		
		div $t0 ,$t1
		mfhi $t2
		j Loop1
CONTINUE :
		
		li $v0 ,5
		syscall
		move $t3 ,$v0
Loop2 : beq $t3 , $t1 ,CORRECT
		la $a0 , wrong_prompt
		li $v0 ,4
		syscall
		
		li $v0,5
		syscall
		move $t3 ,$v0
		j Loop2
CORRECT :
		
		la $a0 ,right_prompt
		li $v0 ,4
		syscall
		
		li $v0 , 10
		syscall		
.data

promptX : .asciiz "Give a : "
promptY : .asciiz "Give b : "
prompt  : .asciiz "Give Maximum Common Divisor : "
wrong_prompt : .asciiz "Wrong answer ,guess again\nEnter the GCD"
right_prompt : .asciiz "Congrats !"