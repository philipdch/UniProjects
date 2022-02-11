onerror {quit -f}
vlib work
vlog -work work Exercise_3.vo
vlog -work work Exercise_3.vt
vsim -novopt -c -t 1ps -L cycloneii_ver -L altera_ver -L altera_mf_ver -L 220model_ver -L sgate work.Exercise_3_vlg_vec_tst
vcd file -direction Exercise_3.msim.vcd
vcd add -internal Exercise_3_vlg_vec_tst/*
vcd add -internal Exercise_3_vlg_vec_tst/i1/*
add wave /*
run -all
