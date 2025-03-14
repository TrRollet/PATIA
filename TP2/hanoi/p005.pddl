(define (problem hanoi-4)
  (:domain hanoi)
  
  (:objects 
    d1 d2 d3 d4 - disc    ; 4 disques
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    ; Relations de taille
    (smaller d1 d2) ; d1 < d2
    (smaller d1 d3) ; d1 < d3
    (smaller d1 d4) ; d1 < d4
    (smaller d2 d3) ; d2 < d3
    (smaller d2 d4) ; d2 < d4
    (smaller d3 d4) ; d3 < d4

    ; États clear initiaux
    (clear d3)
    (clear d1)
    (clear d2)

    ; Positions initiales des disques
    (on d4 t1)
    (on d3 d4)
    (on d1 t2)
    (on d2 t3)

    (handempty) ; main vide
  )

  (:goal 
    (and
      (on d1 d2)
      (on d2 d3)
      (on d3 d4)
      (on d4 t3)
    )
  )
)