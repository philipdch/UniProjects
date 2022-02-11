LIBRARY work, ieee;
USE work.ALU_Components.all;
USE ieee.std_logic_1164.all;

PACKAGE singleBitALU IS
	
	COMPONENT ALU IS
			PORT ( A ,B , Ainvert ,Binvert ,carryIn : IN STD_LOGIC;
								operation :  IN STD_LOGIC_VECTOR(2 DOWNTO 0);
					carryOut , result  : OUT STD_LOGIC);
	END COMPONENT;
	
END 	singleBitALU;

LIBRARY work, ieee;
USE work.ALU_Components.all;
USE ieee.std_logic_1164.all;

	ENTITY ALU IS
		PORT (A ,B , Ainvert ,Binvert ,carryIn : IN STD_LOGIC;
								operation :  IN STD_LOGIC_VECTOR(2 DOWNTO 0);
					carryOut , result  : OUT STD_LOGIC);
	END ALU;
	
	ARCHITECTURE structure OF ALU IS
		SIGNAL muxOut1 ,muxOut2 , ANDOut ,OROut ,AdderOut ,XOROut :STD_LOGIC;
		BEGIN 
			mux1 : multiplexer2to1 PORT MAP (A ,(NOT A), Ainvert ,muxOut1);
			mux2 : multiplexer2to1 PORT MAP (B ,(NOT B), Binvert ,muxOut2);
			ANDOut <= muxOut1 AND muxOut2;
			OROut <=  muxOut2 OR  muxOut1;
			fadder : full_adder PORT MAP (muxOut1 ,muxOut2 ,carryIn ,carryOut ,AdderOut);
			XOROut <= muxOut1 XOR muxOut2;
			mux5to1 : multiplexer5to1 PORT MAP (ANDOut ,OROut ,XOROut ,AdderOut ,muxOut1,operation ,result);
	END structure;