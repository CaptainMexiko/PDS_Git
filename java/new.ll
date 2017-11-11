; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins



define i32 @main() {
; Début block 
%x = alloca i32
%b = alloca i32
%tmp1 = load i32, i32* %x
%tmp2 = add i32 3, %tmp1
store i32 %tmp2, i32*  %x
store i32 0, i32*  %b
; Début du if
%tmp3 = load i32, i32* %x
%icmp1= icmp ne i32 %tmp3, 0 
br i1 %icmp1, label %then3, label %else5
then3:
; Début block 
ret i32 4
; Fin block 
br label %fi4
else5:
; Début block 
; Début du if
%tmp4 = load i32, i32* %x
%tmp5 = add i32 %tmp4, 2
%icmp2= icmp ne i32 %tmp5, 0 
br i1 %icmp2, label %then1, label %fi2
then1:
; Début block 
ret i32 3
; Fin block 
br label %fi2
fi2:
; Fin du if
; Fin block 
br label %fi4
fi4:
; Fin du if
%tmp6 = load i32, i32* %x
ret i32 %tmp6
; Fin block 
}

