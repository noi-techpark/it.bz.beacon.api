create table issue_comment (
                               id int8 not null,
                               created_at timestamp not null,
                               updated_at timestamp not null,
                               issue_id int8 not null,
                               user_username varchar(255) not null,
                               user_name varchar(255) not null,
                               user_id int8,
                               comment text not null,
                               primary key (id)
);

alter table issue_comment add constraint FK_issue_comment_issue_id_issue foreign key (issue_id) references issue;
alter table issue_comment add constraint FK_issue_comment_user_id_user foreign key (user_id) references "user";

alter table issue add column resolved boolean not null default false;
alter table issue add column resolved_at timestamp;

update issue
set resolved = true,
    resolved_at = issue_solution.created_at
from issue_solution
where solution_id is not null and issue.solution_id = issue_solution.id;

insert into issue_comment
select issue_solution.id,
       issue_solution.created_at,
       issue_solution.updated_at,
       i.id as issue_id,
       issue_solution.resolver as user_username,
       u.name || ' ' || u.surname as user_name,
       u.id as user_id,
       solution || case when solution_description is not null and solution <> solution_description and trim(solution_description) <> '' then E'\n\n' || issue_solution.solution_description else '' end as comment
from issue_solution
         join issue i on issue_solution.id = i.solution_id
         left join "user" u on u.username = issue_solution.resolver;