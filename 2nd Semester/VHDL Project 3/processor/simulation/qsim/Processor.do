onerror {quit -f}
vlib work
vlog -work work Processor.vo
vlog -work work Processor.vt
vsim -novopt -c -t 1ps -L cycloneii_ver -L altera_ver -L altera_mf_ver -L 220model_ver -L sgate work.Processor_vlg_vec_tst
vcd file -direction Processor.msim.vcd
vcd add -internal Processor_vlg_vec_tst/*
vcd add -internal Processor_vlg_vec_tst/i1/*
add wave /*
run -all
