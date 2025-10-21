package student.authz

# Example input (for reference only):
# {
#   "user": {"username": "alice", "role": "STUDENT"},
#   "action": "student:update",
#   "resource": {"target": "self"}
# }

default allow := false

# Admins can perform any action
allow if {
    input.user.role == "ADMIN"
}

# Students can update their own info (but not delete)
allow if {
    input.user.role == "STUDENT"
    input.action == "student:update"
    input.resource.target == "self"
}
