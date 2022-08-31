import React from 'react';
import {Outlet} from "react-router-dom";
import BreadcrumbTrail from "../components/BreadcrumbTrail";
import useBreadcrumbs from "use-react-router-breadcrumbs";

export default function Page({ children }) {
    const breadcrumbs = useBreadcrumbs();

    return (
        <main>
            <BreadcrumbTrail breadCrumbs={breadcrumbs} />
            {children ?? <Outlet />}
        </main>
    );
}