%phrase(order(Sem), [μια, μαργαρίτα, με, ντομάτα, χωρίς, κρεμμύδι]).
%phrase(order(Sem), δύο, ιταλικές, χωρίς, ντομάτα, με, κρεμμύδι, και, μια, μαργαρίτα, με, μανιτάρια]).

order([ItemSem]) --> item(ItemSem).
order([Item, Order]) --> item(Item), [and], order(Order).
item([MainSem]) --> main(MainSem).
item([MainSem, Spec1, Spec2]) --> main(MainSem), manyspecs(Spec1, Spec2).
main([Num, Pizza]) --> num(Num), pizza(Pizza).
manyspecs(SpecSem) --> spec(SpecSem).
manyspecs(Spec1, Spec2) --> spec(Spec1), manyspecs(Spec2).
spec([Topping , PrepSem]) --> prep(PrepSem), topping(Topping).
num(1) --> [one].
num(2) --> [two].
num(3) --> [three].
pizza(margargita) --> [margarita].
pizza(margargita) --> [margaritas].
pizza(italian) --> [italian].
pizza(italian) --> [italian].
prep(yes) --> [with].
prep(no) --> [without].
topping(tomato) --> [tomato].
topping(onion) --> [onion].
topping(mushroom) --> [mushroom].