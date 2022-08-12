import React from 'react';
import {Outlet} from "react-router-dom";

export default function Pupils() {
    return (
        <main>
            <h1>Pupils</h1>
            <Outlet />
        </main>
    );
}
