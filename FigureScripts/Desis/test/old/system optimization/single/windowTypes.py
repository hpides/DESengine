import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None

windowType = ['Tumbling', 'Sliding', 'Punctuation', 'Session', 'Countbased']
widthSin = 0.2
widthAll=[widthSin,widthSin,widthSin,widthSin,widthSin]

fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=windowType, y=[3372245.47, 1966341.8, 2238047.26, 2041280.4, 2181275.89], legendrank=1, width=widthAll
                     , marker_line_color='rgb(99,110,250)', marker_pattern_shape="."))
fig.add_trace(go.Bar(name="DesisIC", x=windowType, y=[31256010.27, 31687829.55, 30392116.51, 30565557.23, 15967132.95], legendrank=2, width=widthAll
                     , marker_line_color='rgb(239,85,59)', marker_pattern_shape="/"))
fig.add_trace(go.Bar(name="DesisSW", x=windowType, y=[31572287.21, 31566440.19, 30735626.13, 30871344.42, 16001392.57], legendrank=3, width=widthAll
                     , marker_line_color='rgb(255,161,90)', marker_pattern_shape="-"))
fig.add_trace(go.Bar(name="Desis", x=windowType, y=[32743863.48, 31710032.11, 30776051.23, 30929431.42, 16012225.69], legendrank=4, width=widthAll
                     , marker_line_color='rgb(171,99,250)', marker_pattern_shape="\\"))
# fig.add_trace(go.Bar(name="DesisSw", x=[" "], y=[30545075.4], legendrank=4, width=[0.18]
#                      , marker_line_color='rgb(255,161,90)', marker_pattern_shape="-"))
# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6)


#legend
fig.update_layout(
    # legend=dict(
    #     yanchor="top",
    #     y=0.99,
    #     xanchor="left",
    #     x=0.01,
    #     # bordercolor="Black",
    #     # borderwidth=2,
    #     # bgcolor="white",
    #     font=dict(
    #         size=10,
    #         color="black"
    #     ),
    # ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=15),
        exponentformat="e",
        ticktext=["0", "2M", "5M", "10M", "20M", "30M"],
        tickvals=[0, 2000000, 5000000, 10000000, 20000000, 30000000],
        tickmode="array",
        range=[0, 35000000],
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=600,
    height=500,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)'
)
# fig = px.bar(x=["a","b","c"], y=[1,3,2], color=["red", "goldenrod", "#00D"], color_discrete_map="identity")
fig.update_layout(barmode='group', bargap=0.2, bargroupgap=0.0)

# fig.update_yaxes(automargin=True)
# fig.update_yaxes(ticks="outside", tickwidth=1, tickcolor='black', ticklen=5)
fig.update_xaxes(showline=True, linewidth=1, linecolor='black', mirror=True)
fig.update_yaxes(showline=True, linewidth=1, linecolor='black', mirror=True)


fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\single\singleWindow.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\single\singleWindow.svg")