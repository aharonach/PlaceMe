import React from "react";
import { Button } from "react-bootstrap";

export default function DeleteAttribute({ attributeId, handleDelete }) {
    return (
        <Button variant="danger" size="sm" onClick={() => handleDelete(attributeId)}>
            Delete
        </Button>
    )
}