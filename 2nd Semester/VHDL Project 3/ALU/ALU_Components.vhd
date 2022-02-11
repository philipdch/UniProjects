LIBRARY ieee;
USE ieee.std_logic_1164.all;

PACKAGE ALU_Components IS --creates a package which contains every component used in the 1-bit ALU
	COMPONENT multiplexer2to1 IS
		PORT (inputA , inputB ,s : IN STD_LOGIC;
							f  : OUT STD_LOGIC);
	END COMPONENT;
	
	COMPONENT full_adder IS
		PORT (A , B , carryIn : IN STD_LOGIC;
						carryOut , sum  : OUT STD_LOGIC);
	END COMPONENT;
	
	COMPONENT multiplexer4to1 IS
		PORT (inputA , inputB ,inputC ,inputD : IN STD_LOGIC;
								s     : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
								F     : OUT STD_LOGIC );
	END COMPONENT;
	
	COMPONENT multiplexer5to1 IS
		PORT (A ,B ,C ,D ,E  : IN STD_LOGIC;
						S : IN STD_LOGIC_VECTOR(2 DOWNTO 0);
						F : OUT STD_LOGIC);
	END COMPONENT;
END ALU_Components;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

	ENTITY multiplexer2to1 IS
		PORT (inputA ,inputB ,s :IN STD_LOGIC ;
							f        :OUT STD_LOGIC);
	END multiplexer2to1;

	ARCHITECTURE behaviour OF multiplexer2to1 IS
		BEGIN 
				f <= (inputA AND (NOT s)) OR (inputB AND s);
	END behaviour;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

	ENTITY full_adder IS
		PORT (A , B , carryIn : IN STD_LOGIC;
					carryOut ,sum : OUT STD_LOGIC);
	END full_adder;
	
	ARCHITECTURE behaviour OF full_adder IS 
		BEGIN
			sum <= (A AND (NOT B) AND (NOT carryIn)) OR ((NOT A) AND B AND (NOT carryIn)) OR 
					 ((NOT A) AND (NOT B) AND carryIn) OR (A AND B AND carryIn);
			carryOut <= (B AND carryIn) OR (A AND carryIn) OR (A AND B);
	END behaviour;

LIBRARY ieee ,work;
USE ieee.std_logic_1164.all;
USE work.ALU_Components.multiplexer2to1;

	ENTITY multiplexer4to1 IS
		PORT (inputA , inputB ,inputC ,inputD : IN STD_LOGIC;
								s     : IN STD_LOGIC_VECTOR(1 DOWNTO 0);
								F     : OUT STD_LOGIC );
	END multiplexer4to1;
	
	ARCHITECTURE structure OF multiplexer4to1 IS
		SIGNAL muxOut1 ,muxOut2 : STD_LOGIC;
			BEGIN 
				mux1 : multiplexer2to1 PORT MAP(inputA ,inputB ,s(0),muxOut1);
				mux2 : multiplexer2to1 PORT MAP(inputC ,inputD ,s(0),muxOut2);
				muxOut : multiplexer2to1 PORT MAP(muxOut1 ,muxOut2 ,s(1),f);
	END structure;

LIBRARY ieee ,work;
USE ieee.std_logic_1164.all;
	
	ENTITY multiplexer5to1 IS
		PORT ( A, B ,C ,D ,E : IN STD_LOGIC;
							S  : IN STD_LOGIC_VECTOR(2 DOWNTO 0);
							F  : OUT STD_LOGIC);
	END multiplexer5to1;
	
	ARCHITECTURE behaviour OF multiplexer5to1 IS
	BEGIN
		WITH S SELECT 
			F <= A WHEN "000",
				  B WHEN "001",
				  C WHEN "010",
				  D WHEN "011",
				  E WHEN OTHERS;
	END behaviour;