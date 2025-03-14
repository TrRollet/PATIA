(define (problem hanoi-7)
  (:domain hanoi)
  
  (:objects 
    d1 d2 d3 d4 d5 d6 d7 - disc    ; 7 disques
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    ; Relations de taille
    (smaller d1 d2) ; d1 < d2
    (smaller d1 d3) ; d1 < d3
    (smaller d1 d4) ; d1 < d4
    (smaller d1 d5) ; d1 < d5
    (smaller d1 d6) ; d1 < d6
    (smaller d1 d7) ; d1 < d7
    (smaller d2 d3) ; d2 < d3
    (smaller d2 d4) ; d2 < d4
    (smaller d2 d5) ; d2 < d5
    (smaller d2 d6) ; d2 < d6
    (smaller d2 d7) ; d2 < d7
    (smaller d3 d4) ; d3 < d4
    (smaller d3 d5) ; d3 < d5
    (smaller d3 d6) ; d3 < d6
    (smaller d3 d7) ; d3 < d7
    (smaller d4 d5) ; d4 < d5
    (smaller d4 d6) ; d4 < d6
    (smaller d4 d7) ; d4 < d7
    (smaller d5 d6) ; d5 < d6
    (smaller d5 d7) ; d5 < d7
    (smaller d6 d7) ; d6 < d7

    ; États clear initiaux
    (clear d1)
    (clear t2)
    (clear t3)

    ; Positions initiales des disques
    (on d7 t1)
    (on d6 d7)
    (on d5 d6)
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
      (on d5 d6)
      (on d6 d7)
      (on d7 t3)
    )
  )
)