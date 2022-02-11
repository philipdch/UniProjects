library verilog;
use verilog.vl_types.all;
entity ALU_16_Bit_vlg_check_tst is
    port(
        overflow        : in     vl_logic;
        result          : in     vl_logic_vector(15 downto 0);
        sampler_rx      : in     vl_logic
    );
end ALU_16_Bit_vlg_check_tst;
