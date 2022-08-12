import React from 'react';
import {Outlet} from "react-router-dom";

export default function Placements() {
    return (
        <main>
            <h1>Placements</h1>
            <Outlet />
        </main>
    );
}
