library verilog;
use verilog.vl_types.all;
entity Processor is
    port(
        DIN             : in     vl_logic_vector(15 downto 0);
        Clock           : in     vl_logic;
        Run             : in     vl_logic;
        resetn          : inout  vl_logic;
        Done            : out    vl_logic;
        BusWires        : out    vl_logic_vector(15 downto 0)
    );
end Processor;
