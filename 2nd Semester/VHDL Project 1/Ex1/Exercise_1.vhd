LIBRARY ieee, work;
USE work.exercise_components.all;
USE ieee.std_logic_1164.all;

ENTITY Exercise_1 IS
	PORT (X1 ,X2 ,X3 ,X4 ,X5 : IN STD_LOGIC;
				 F , G          : OUT STD_LOGIC);
END Exercise_1;

ARCHITECTURE exStructure OF Exercise_1 IS
	SIGNAL  comSum1 ,comSum2 ,fSum1 ,fSum2 ,fSum3 ,gSum1 ,gSum2 ,gSum3 : STD_LOGIC;
		BEGIN
			fpos1 : myOR2 PORT MAP(X3 ,X5 ,fSum1);
			fpos2 : myOR2 PORT MAP((NOT X1), X2 ,fSum2);
			fpos3 : myOR2 PORT MAP((NOT X3), (NOT X4), fSum3);
			comPos1 : myOR3 PORT MAP(X2 ,(NOT X4), (NOT X5), comSum1);
			comPos2 : myOR3 PORT MAP((NOT X2) ,X4 ,(NOT X5) ,comSum2);
			gpos1 : myOR2 PORT MAP((NOT X1), (NOT X5), gSum1);
			gpos2 : myOR3 PORT MAP((NOT X1), (NOT X3) ,(NOT X4) ,gSum2);
			gpos3 : myOR4 PORT MAP(X1 ,X2 ,(NOT X3), (NOT X4) ,gSum3);
			F <= fSum1 AND fSum2 AND fSum3 AND comSum1 AND comSum2;
			G <= gSum1 AND gSum2 AND gSum3 AND comSum1 AND comSum2;
END exStructure;
					