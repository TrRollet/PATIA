(define (domain sokoban)
  	(:requirements :strips :typing)

	(:types
		case - object
	)

	(:predicates
		(player ?c - case)
		(box ?c - case)
		(clear ?c - case)
		(aligned-left ?c - case ?c2 - case)
		(aligned-up ?c - case ?c2 - case)
	)

	(:action push-left
		:parameters (?c1 - case ?c2 - case ?c3 - case)
		:precondition (and 
		(player ?c1) 
		(box ?c2) 
		(aligned-left ?c3 ?c2) 
		(aligned-left ?c2 ?c1) 
		(clear ?c3))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2) 
		(not (box ?c2)) 
		(box ?c3) (not 
		(clear ?c3)) 
		(clear ?c2))
	)


	(:action push-right
		:parameters (?c1 - case ?c2 - case ?c3 - case)
		:precondition (and 
		(player ?c1) 
		(box ?c2) 
		(aligned-left ?c1 ?c2) 
		(aligned-left ?c2 ?c3) 
		(clear ?c3))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2) 
		(not (box ?c2)) 
		(box ?c3) 
		(not (clear ?c3)) 
		(clear ?c2))
	)

	(:action push-up
		:parameters (?c1 - case ?c2 - case ?c3 - case)
		:precondition (and 
		(player ?c1) 
		(box ?c2) 
		(aligned-up ?c3 ?c2) 
		(aligned-up ?c2 ?c1) 
		(clear ?c3))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2) 
		(not (box ?c2)) 
		(box ?c3) 
		(not (clear ?c3)) 
		(clear ?c2))
	)


	(:action push-down
		:parameters (?c1 - case ?c2 - case ?c3 - case)
		:precondition (and 
		(player ?c1) 
		(box ?c2) 
		(aligned-up ?c1 ?c2) 
		(aligned-up ?c2 ?c3) 
		(clear ?c3))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2) 
		(not (box ?c2)) 
		(box ?c3) 
		(not (clear ?c3)) 
		(clear ?c2))
	)

	(:action move-left
		:parameters (?c1 - case ?c2 - case)
		:precondition (and 
		(player ?c1) 
		(aligned-left ?c2 ?c1) 
		(clear ?c2))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2))
	)

	
	(:action move-right
		:parameters (?c1 - case ?c2 - case)
		:precondition (and 
		(player ?c1) 
		(aligned-left ?c1 ?c2) 
		(clear ?c2))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2))
	)

	(:action move-up
		:parameters (?c1 - case ?c2 - case)
		:precondition (and 
		(player ?c1) 
		(aligned-up ?c2 ?c1) 
		(clear ?c2))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2))
	)

	(:action move-down
		:parameters (?c1 - case ?c2 - case)
		:precondition (and 
		(player ?c1) 
		(aligned-up ?c1 ?c2) 
		(clear ?c2))

		:effect (and 
		(not (player ?c1)) 
		(player ?c2))
	)
)