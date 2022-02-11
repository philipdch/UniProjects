LIBRARY ieee;
USE ieee.std_logic_1164.all;

PACKAGE exercise_components IS
	COMPONENT myOR2
		PORT (inX1 ,inX2   : IN STD_LOGIC; 
            outF       : OUT STD_LOGIC );
   END COMPONENT;
	
	COMPONENT myOR3
	   PORT (inX1, inX2, inX3 : IN STD_LOGIC;
		         outF      : OUT STD_LOGIC  );
	END COMPONENT;
	
	COMPONENT myOR4
	   PORT (inX1, inX2, inX3, inX4 : IN  STD_LOGIC;
	            outF          :OUT  STD_LOGIC);
	END COMPONENT;
END exercise_components;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

	ENTITY myOR2 IS
		PORT (inX1 ,inX2 : IN STD_LOGIC;
					outF       : OUT STD_LOGIC);
	END myOR2;
	ARCHITECTURE logic1 OF myOR2 IS
		BEGIN 
			outF <= inX1 OR inX2;
	END logic1;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

	ENTITY myOR3 IS
		PORT (inX1 ,inX2 ,inX3 : IN STD_LOGIC;
						outF          : OUT STD_LOGIC);
	END myOR3;
	ARCHITECTURE logic2 OF myOR3 IS
		BEGIN 
			outF <= inX1 OR inX2 OR inX3;
	END logic2;

LIBRARY ieee;
USE ieee.std_logic_1164.all;

	ENTITY myOR4 IS
		PORT (inX1 ,inX2 ,inX3 ,inX4 : IN STD_LOGIC;
							outF          :OUT STD_LOGIC);
	END myOR4;
	ARCHITECTURE logic3 OF myOR4 IS
		BEGIN 
			outF <= inX1 OR inX2 OR inX3 OR inX4;
	END logic3;
	