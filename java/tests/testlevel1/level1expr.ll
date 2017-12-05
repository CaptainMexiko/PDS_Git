; Target
target triple = "x86_64-unknown-linux-gnu"
; External declaration of the printf function
declare i32 @printf(i8* noalias nocapture, ...)

; Actual code begins

@"formatPrint1" = global [12 x i8] c"%d+%d = %d\0A\00"
@"formatPrint2" = global [12 x i8] c"%d-%d = %d\0A\00"
@"formatPrint3" = global [12 x i8] c"%d*%d = %d\0A\00"
@"formatPrint4" = global [12 x i8] c"%d/%d = %d\0A\00"
@"formatPrint5" = global [12 x i8] c"%d+%d = %d\0A\00"
@"formatPrint6" = global [18 x i8] c"%d* (%d+%d) = %d\0A\00"
@"formatPrint7" = global [18 x i8] c"%d*  %d+%d  = %d\0A\00"


define void @main(){
; Début block 
%tmp1 = add i32 5, 7
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint1, i32 0, i32 0), i32 5, i32 7, i32 %tmp1)
%tmp2 = sub i32 5, 7
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint2, i32 0, i32 0), i32 5, i32 7, i32 %tmp2)
%tmp3 = mul i32 5, 7
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint3, i32 0, i32 0), i32 5, i32 7, i32 %tmp3)
%tmp4 = udiv i32 5, 7
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint4, i32 0, i32 0), i32 5, i32 7, i32 %tmp4)
%tmp5 = add i32 5, 1
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([12 x i8],[12 x i8]* @formatPrint5, i32 0, i32 0), i32 5, i32 1, i32 %tmp5)
%tmp6 = add i32 5, 7
%tmp7 = mul i32 5, %tmp6
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([18 x i8],[18 x i8]* @formatPrint6, i32 0, i32 0), i32 5, i32 5, i32 7, i32 %tmp7)
%tmp8 = mul i32 5, 5
%tmp9 = add i32 %tmp8, 7
call i32 (i8*,...) @printf(i8* getelementptr inbounds ([18 x i8],[18 x i8]* @formatPrint7, i32 0, i32 0), i32 5, i32 5, i32 7, i32 %tmp9)
; Fin block 
ret void
}
