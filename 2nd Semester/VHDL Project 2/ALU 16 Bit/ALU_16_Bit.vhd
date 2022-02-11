library work ,ieee;
USE ieee.std_logic_1164.all;
USE work.singleBitALU.all;

PACKAGE ALU16Bit IS
	COMPONENT ALU_16_Bit IS
		PORT ( A :		 IN std_logic_vector(15 DOWNTO 0);
				B : 	 	 IN std_logic_vector(15 DOWNTO 0);
				OPCODE :   IN STD_LOGIC_VECTOR(2 DOWNTO 0);
				overflow : OUT STD_logic;
				result :   OUT std_logic_vector(15 downto 0));
	END COMPONENT;
END ALU16Bit;

library work ,ieee;
USE ieee.std_logic_1164.all;
USE work.singleBitALU.all;

	ENTITY ALU_16_Bit IS
		PORT( A :		 IN std_logic_vector(15 DOWNTO 0);
				B : 	 	 IN std_logic_vector(15 DOWNTO 0);
				OPCODE :   IN STD_LOGIC_VECTOR(2 DOWNTO 0);
				overflow : OUT STD_logic;
				result :   OUT std_logic_vector(15 downto 0));
	END ALU_16_Bit;
	
	ARCHITECTURE structure OF ALU_16_Bit IS
			SIGNAL Ainvert ,Binvert ,tempOverflow:STD_LOGIC; -- Invert A or/and B if required
			SIGNAL operation : STD_LOGIC_VECTOR(2 DOWNTO 0); -- Input in every 1-bit ALU's 4to1 multiplexer . Picks a certain action
			SIGNAL carry : STD_LOGIC_VECTOR(15 DOWNTO 0); -- Transmits through each ALU's full-adder .CarryOut of full-adder i, becomes CarryIn in full-adder i+1
		BEGIN 
			PROCESS (OPCODE) -- Gives a value to Ainvert ,Binvert ,operation and CarryIn ,depending on the OPCODE that is given
			BEGIN
					operation <= "000"; --AND 
					Ainvert <= '0';
					Binvert <= '0';
					carry(0) <= '0';
				IF OPCODE = "001" THEN --OR
					operation <= "001";
					Ainvert <= '0';
					Binvert <= '0';
					carry(0) <= '0';
				ELSIF OPCODE= "011" THEN --ADD
					operation <= "011";
					Ainvert <= '0';
					Binvert <= '0';
					carry(0) <= '0';
				ELSIF OPCODE = "010" THEN --SUB
					operation <= "011";
					Ainvert <= '0';
					Binvert <= '1';
					carry(0) <= '1';
				ELSIF OPCODE = "101" THEN --NOR 
					operation <= "000";
					Ainvert <= '1';
					Binvert <= '1';
					carry(0) <= '0';
				ELSIF OPCODE = "100" THEN --XOR
					operation <="010";
					Ainvert <= '0';
					Binvert <= '0';
					carry(0) <= '0';
				ELSIF OPCODE = "110" THEN --NOT 
					operation <= "101";
					Ainvert <= '1';
					Binvert <= '0';
					carry(0)<= '0';
				END IF;
			END PROCESS;
			ALULoop : FOR i IN 0 TO 14 GENERATE -- creates 15 different ALUs, where each produces an 1-bit result 
					alu_1_bit : ALU PORT MAP (A(i),B(i) ,Ainvert , Binvert ,carry(i) , operation ,carry(i+1) , result(i));
			END GENERATE;
			lastALU : ALU PORT MAP (A(15),B(15),Ainvert ,Binvert ,carry(15), operation ,tempOverflow ,result(15)); --16th and last ALU creates the last bit in result and gives a value to signal "overflow"
			WITH operation SELECT -- overflow only matters in ADD and SUB
				overflow <= tempOverflow WHEN "011",
								'0' WHEN OTHERS;
		END structure;