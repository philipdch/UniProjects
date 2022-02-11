library verilog;
use verilog.vl_types.all;
entity ALU_16_Bit is
    port(
        A               : in     vl_logic_vector(15 downto 0);
        B               : in     vl_logic_vector(15 downto 0);
        OPCODE          : in     vl_logic_vector(2 downto 0);
        overflow        : out    vl_logic;
        result          : out    vl_logic_vector(15 downto 0)
    );
end ALU_16_Bit;
