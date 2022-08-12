import React from 'react';
import {Outlet} from "react-router-dom";

export default function Templates() {
    return (
        <main>
            <h1>Templates</h1>
            <Outlet />
        </main>
    );
}
