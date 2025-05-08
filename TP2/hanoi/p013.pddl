(define (problem hanoi-0)
  (:domain hanoi)
  
  (:objects 
     - disc    ; 0 disques
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
      (on d0 t3)
    )
  )
)