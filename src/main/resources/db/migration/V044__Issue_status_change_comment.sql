alter table issue_comment add column status_change boolean not null default false;
truncate issue_comment;

insert into issue_comment
select row_number() over (order by issue_solution.created_at, n),
       issue_solution.created_at,
       issue_solution.updated_at,
       i.id as issue_id,
       issue_solution.resolver as user_username,
       u.name || ' ' || u.surname as user_name,
       u.id as user_id,
       case when n = 2 then issue_solution.resolver || ' closed on ' || issue_solution.created_at::date else
           solution || case when solution_description is not null and solution <> solution_description and trim(solution_description) <> '' then E'\n\n' || issue_solution.solution_description else '' end end as comment,
       n = 2 as status_change
from issue_solution
         join issue i on issue_solution.id = i.solution_id
         join generate_series(1,2) n on true
         left join "user" u on u.username = issue_solution.resolver