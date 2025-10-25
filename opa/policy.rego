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

# ===== FIELD-LEVEL ACCESS =====
visible_fields = fields if {
    input.user.role == "ADMIN"
    fields := ["id", "name", "email", "grade", "address"]
}

visible_fields = fields if {
    input.user.role == "STUDENT"
    fields := ["id", "name", "email"]  # student cannot see 'grade' or 'address'
}

editable_fields = fields if {
    input.user.role == "STUDENT"
    fields := ["email"]  # student can only edit email
}
