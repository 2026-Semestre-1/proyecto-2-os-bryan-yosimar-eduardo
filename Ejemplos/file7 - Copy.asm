MOV DX, "prueba.txt"
MOV AH, 3CH
INT 21H

MOV DX, "prueba.txt"
MOV AL, "Hola "
MOV AH, 40H
INT 21H

MOV DX, "prueba.txt"
MOV AL, "mundo"
MOV AH, 40H
INT 21H

MOV DX, "prueba.txt"
MOV AH, 4DH
INT 21H

MOV DX, AL
MOV AH, 10H
INT 21H

MOV DX, "prueba.txt"
MOV AH, 41H
INT 21H

MOV DX, "prueba.txt"
MOV AH, 3DH
INT 21H



