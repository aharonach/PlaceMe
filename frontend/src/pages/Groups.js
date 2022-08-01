import React from 'react';
import {LinkContainer} from "react-router-bootstrap";
import {Button} from "react-bootstrap";
import {Outlet} from "react-router-dom";

export default function Groups() {
    return (
        <main>
            <h1>Groups</h1>
            <LinkContainer to="add"><Button>Add Group</Button></LinkContainer>
            <Outlet />
        </main>
    );
}
