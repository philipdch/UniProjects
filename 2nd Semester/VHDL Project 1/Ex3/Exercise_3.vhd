ENTITY Exercise_3 IS
	PORT (X1,X2,X3   :  IN BIT;
			F          :  OUT BIT);
END Exercise_3;

ARCHITECTURE Arch_Exercise_3 OF Exercise_3 IS
	BEGIN 
		F <=( X2 OR ((NOT X1) AND X3));
END Arch_Exercise_3;