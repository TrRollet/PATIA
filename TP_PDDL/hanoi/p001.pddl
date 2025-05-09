(define (problem hanoi-3)
  (:domain hanoi)
  
  (:objects 
    d1 d2 d3 - disc    ; 3 disques (d1 plus petit, d3 plus grand)
    t1 t2 t3 - tige    ; 3 tiges
  )

  (:init
    (smaller d1 d2) ; d1 < d2
    (smaller d1 d3) ; d1 < d3
    (smaller d2 d3) ; d2 < d3
    
		; d1, t2 et t3 sont libres
    (clear d1)
    (clear t2)          
    (clear t3)
    
    (on d1 d2) ; d1 est sur d2
    (on d2 d3) ; d2 est sur d3
    (on d3 t1) ; d3 est sur la tige 1
    
    (handempty) ; main vide
  )

  (:goal 
    (and
			; d1, d2 et d3 sont sur t3 dans le bon ordre
      (on d1 d2)
      (on d2 d3)
      (on d3 t3)
    )
  )
)