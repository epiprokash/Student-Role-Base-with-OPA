package student.authz
# input: {"user": {"username": "...", "role": "..."}, "action": "...",
"resource": {...}}
default allow = false
allow {
    input.user.role == "ADMIN"
}
# Students can update their own info (but not delete)
allow {
    input.user.role == "STUDENT"
    input.action == "student:update"
    input.resource.target == "self"
}
# Admins can delete students (already covered by admin rule)
