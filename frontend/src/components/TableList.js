import React from 'react';
import {Link} from "react-router-dom";
import {Alert, Table} from "react-bootstrap";

function TableList({
           items,
           columns,
           basePath = '',
           bsProps = { bordered: true, hover: true }
       }) {

    if ( ! items || items.length <= 0 ) {
        return (
            <Alert variant="info">Nothing to show.</Alert>
        )
    }

    return (
        <Table {...bsProps}>
            <thead>
                <tr>
                    {columns && Object.keys(columns).map( key => {
                        if ( 'actions' === key ) {
                            return <th key={key}>{columns[key].label}</th>;
                        }

                        return <th key={key}>{columns[key]}</th>
                    })}
                </tr>
            </thead>
            <tbody>
            {items && items.map( item => (
                <tr key={item['id']}>
                    {Object.keys(columns).map( key => {
                        // return empty cell for the moment.
                        if ( 'id' === key ) {
                            return <td key={key}><Link to={basePath + item[key]}>{item[key]}</Link></td>
                        }

                        if ( 'actions' === key ) {
                            return <td key={key}>{columns[key].callbacks.map( callback => callback(item))}</td>
                        }

                        if ( ['undefined', 'object'].includes( typeof( item[key] ) ) ) {
                            return <td key={key}></td>;
                        }

                        return <td key={key}>{item[key]}</td>
                    })}
                </tr>
            ))}
            </tbody>
        </Table>
    );
}

export default TableList;