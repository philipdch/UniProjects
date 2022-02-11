library verilog;
use verilog.vl_types.all;
entity ALU_vlg_sample_tst is
    port(
        A               : in     vl_logic;
        Ainvert         : in     vl_logic;
        B               : in     vl_logic;
        Binvert         : in     vl_logic;
        carryIn         : in     vl_logic;
        operation       : in     vl_logic_vector(2 downto 0);
        sampler_tx      : out    vl_logic
    );
end ALU_vlg_sample_tst;
