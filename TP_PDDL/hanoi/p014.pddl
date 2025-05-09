(define (problem hanoi--1)
  (:domain hanoi)
  
  (:objects 
     - disc    ; -1 disques
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    ; Relations de taille

    ; États clear initiaux
    (clear t1)
    (clear t2)
    (clear t3)

    ; Positions initiales des disques

    (handempty) ; main vide
  )

  (:goal 
    (and
      (on d-1 t3)
    )
  )
)