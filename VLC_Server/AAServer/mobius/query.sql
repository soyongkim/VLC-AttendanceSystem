ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '1111';
flush privileges;

select * from mobiusdb.cnt;
select * from mobiusdb.lookup;
select * from mobiusdb.ae;
select * from mobiusdb.cin;
select * from mobiusdb.csr;

select lbl from mobiusdb.lookup where ri like "%cnt-info";

select apn, api from mobiusdb.ae where ri like "%std_%";

select con from cin where pi="/Mobius/std_2018220889/cnt-state" && ri=(select max(ri) from cin);

select con from cin where pi="/Mobius/std_2013097010/cnt-state" && ri=(select max(ri) from cin);

with a as (select * from cin where pi="/Mobius/std_2018220889/cnt-state" order by ri desc) select ri,con from a limit 1;



select * from cin where pi="/Mobius/std_2018220889/cnt-state";