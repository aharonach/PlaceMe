import React from 'react';
import {Outlet} from "react-router-dom";

export default function Groups() {
    return (
        <main>
            <h1>Groups</h1>
            <Outlet />
        </main>
    );
}
