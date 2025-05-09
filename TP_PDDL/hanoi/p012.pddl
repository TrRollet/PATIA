(define (problem hanoi-10)
  (:domain hanoi)
  
  (:objects 
    d1 d2 d3 d4 d5 d6 d7 d8 d9 d10 - disc    ; 10 disques
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
    (smaller d1 d8) ; d1 < d8
    (smaller d1 d9) ; d1 < d9
    (smaller d1 d10) ; d1 < d10
    (smaller d2 d3) ; d2 < d3
    (smaller d2 d4) ; d2 < d4
    (smaller d2 d5) ; d2 < d5
    (smaller d2 d6) ; d2 < d6
    (smaller d2 d7) ; d2 < d7
    (smaller d2 d8) ; d2 < d8
    (smaller d2 d9) ; d2 < d9
    (smaller d2 d10) ; d2 < d10
    (smaller d3 d4) ; d3 < d4
    (smaller d3 d5) ; d3 < d5
    (smaller d3 d6) ; d3 < d6
    (smaller d3 d7) ; d3 < d7
    (smaller d3 d8) ; d3 < d8
    (smaller d3 d9) ; d3 < d9
    (smaller d3 d10) ; d3 < d10
    (smaller d4 d5) ; d4 < d5
    (smaller d4 d6) ; d4 < d6
    (smaller d4 d7) ; d4 < d7
    (smaller d4 d8) ; d4 < d8
    (smaller d4 d9) ; d4 < d9
    (smaller d4 d10) ; d4 < d10
    (smaller d5 d6) ; d5 < d6
    (smaller d5 d7) ; d5 < d7
    (smaller d5 d8) ; d5 < d8
    (smaller d5 d9) ; d5 < d9
    (smaller d5 d10) ; d5 < d10
    (smaller d6 d7) ; d6 < d7
    (smaller d6 d8) ; d6 < d8
    (smaller d6 d9) ; d6 < d9
    (smaller d6 d10) ; d6 < d10
    (smaller d7 d8) ; d7 < d8
    (smaller d7 d9) ; d7 < d9
    (smaller d7 d10) ; d7 < d10
    (smaller d8 d9) ; d8 < d9
    (smaller d8 d10) ; d8 < d10
    (smaller d9 d10) ; d9 < d10

    ; États clear initiaux
    (clear d1)
    (clear t2)
    (clear t3)

    ; Positions initiales des disques
    (on d10 t1)
    (on d9 d10)
    (on d8 d9)
    (on d7 d8)
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
      (on d7 d8)
      (on d8 d9)
      (on d9 d10)
      (on d10 t3)
    )
  )
)