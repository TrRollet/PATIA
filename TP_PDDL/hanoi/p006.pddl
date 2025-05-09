(define (problem hanoi-5)
  (:domain hanoi)
  
  (:objects 
    d1 d2 d3 d4 d5 - disc    ; 5 disques
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    ; Relations de taille
    (smaller d1 d2) ; d1 < d2
    (smaller d1 d3) ; d1 < d3
    (smaller d1 d4) ; d1 < d4
    (smaller d1 d5) ; d1 < d5
    (smaller d2 d3) ; d2 < d3
    (smaller d2 d4) ; d2 < d4
    (smaller d2 d5) ; d2 < d5
    (smaller d3 d4) ; d3 < d4
    (smaller d3 d5) ; d3 < d5
    (smaller d4 d5) ; d4 < d5

    ; États clear initiaux
    (clear d1)
    (clear t2)
    (clear t3)

    ; Positions initiales des disques
    (on d5 t1)
    (on d4 d5)
    (on d3 d4)
    (on d2 d3)
    (on d1 d2)

    (handempty) ; main vide
  )

  (:goal 
    (and
      (on d1 d2)
      (on d2 d3)
      (on d3 d4)
      (on d4 d5)
      (on d5 t3)
    )
  )
)