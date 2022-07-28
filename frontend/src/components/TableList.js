import React from 'react';
import {Link} from "react-router-dom";
import {Table} from "react-bootstrap";

function TableList({ items, columns, basePath = '' }) {
    return (
        <Table bordered hover>
            <thead>
                <tr>
                    {columns && Object.keys(columns).map( key => (
                        <th key={key}>{columns[key]}</th>
                    ))}
                </tr>
            </thead>
            <tbody>
            {items && items.map( item => (
                <tr key={item['id']}>
                    {Object.keys(columns).map( key => {
                        // return empty cell for the moment.
                        if ( 'undefined' === typeof( item[key] ) || 'object' === typeof( item[key] ) ) {
                            return <td key={key}></td>;
                        }

                        if ( 'id' === key ) {
                            return <td key={key}><Link to={basePath + item[key]}>{item[key]}</Link></td>
                        }

                        return <td key={key}>{item[key]}</td>
                    })}
                </tr>
            ))}
            {!items && <tr><td colSpan={columns.length}>Nothing to show!</td></tr>}
            </tbody>
        </Table>
    );
}

export default TableList;