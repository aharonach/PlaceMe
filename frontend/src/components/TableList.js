import React from 'react';
import {Link} from "react-router-dom";
import {Alert, Table} from "react-bootstrap";

function TableList({
           items,
           columns,
           linkTo = {field: 'id', basePath: ''},
           numbering= {enabled: true, startFrom: 1},
           bsProps = { bordered: true, hover: true }
       }) {

    if ( ! items || items.length <= 0 ) {
        return (
            <Alert variant="info">Nothing to show.</Alert>
        )
    }

    let currentNumber = numbering.startFrom ? Number(numbering.startFrom) : 1;

    return (
        <Table {...bsProps}>
            <thead>
                <tr>
                    {numbering.enabled && <th>#</th>}

                    {columns && Object.keys(columns).map( key => {
                        if ( 'actions' === key ) {
                            return <th scope="col" key={key}>{columns[key].label}</th>;
                        }

                        return <th scope="col" key={key}>{columns[key]}</th>
                    })}
                </tr>
            </thead>
            <tbody>
            {items && items.map( item => (
                <tr key={item['id']}>
                    {numbering.enabled && <th scope="row">{currentNumber++}</th>}

                    {Object.keys(columns).map( key => {
                        // return empty cell for the moment.
                        if ( linkTo.field === key ) {
                            return <td key={key}><Link to={linkTo.basePath + item['id']}>{item[key]}</Link></td>
                        }

                        if ( 'actions' === key ) {
                            return <td key={key}>{columns[key].callbacks.map( callback => callback(item))}</td>
                        }

                        if ( ['undefined', 'object'].includes( typeof( item[key] ) ) && ! React.isValidElement( item[key] ) ) {
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